package com.ip.api.service;

import com.ip.api.apiPayload.code.ErrorCode;
import com.ip.api.apiPayload.exception.NotFoundException;
import com.ip.api.domain.Attendence;
import com.ip.api.domain.Payroll;
import com.ip.api.domain.User;
import com.ip.api.dto.payroll.PayrollRequest.CreatePayrollDTO;
import com.ip.api.dto.payroll.PayrollResponse.PayrollIdDTO;
import com.ip.api.dto.payroll.PayrollResponse.PersonalPayrollDTO;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.repository.AttendanceRepository;
import com.ip.api.repository.PayrollRepository;
import com.ip.api.repository.UserRepository;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;


    public PayrollIdDTO createPayroll(CreatePayrollDTO request) {
        // 1. 사용자 정보 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        int year = request.getYear();
        int month = request.getMonth();
        // "2024-10" 형식의 문자열을 LocalDate로 변환
        YearMonth yearMonth = YearMonth.of(year, month);
        // 해당 월의 첫 날 00:00:00로 LocalDateTime 생성
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();

        // 해당 월의 마지막 날 23:59:59로 LocalDateTime 생성
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // 2. 월간 출퇴근 데이터 조회 (request의 귀속 연월에 해당하는 데이터만 가져옴)
        List<Attendence> attendences = attendanceRepository.findByUserUserIdAndCreatedAtBetween(
                user.getUserId(), startOfMonth, endOfMonth);

        // 3. 변수 초기화
        long totalLateMinutes = 0;
        long totalEarlyLeaveMinutes = 0;
        long totalWorkMinutes = 0;
        long totalNightWorkMinutes = 0;
        ;
        LocalTime standardCheckInTime = LocalTime.of(9, 0);  // 기준 출근 시간
        LocalTime standardCheckOutTime = LocalTime.of(18, 0); // 기준 퇴근 시간

        for (Attendence attendence : attendences) {
            if (attendence.getCheckInTime() != null && attendence.getCheckOutTime() != null) {
                // 출근 시간이 9시 이전이라면 9시로 설정
                LocalTime checkInTime = attendence.getCheckInTime().toLocalTime();
                if (checkInTime.isBefore(standardCheckInTime)) {
                    checkInTime = standardCheckInTime;
                }

                if (checkInTime.isAfter(standardCheckInTime)) {
                    totalLateMinutes += Duration.between(standardCheckInTime, checkInTime).toMinutes();
                }

                // 출근 시간과 퇴근 시간의 차이를 계산
                LocalDateTime checkInDateTime = LocalDateTime.of(attendence.getCheckInTime().toLocalDate(), checkInTime);
                LocalDateTime checkOutDateTime = LocalDateTime.of(attendence.getCheckOutTime().toLocalDate(), standardCheckOutTime);

                // 총 근무 시간 계산
                totalWorkMinutes += Duration.between(checkInDateTime, checkOutDateTime).toMinutes();

                // 야근 시간 계산 (18시 이후의 시간만 추가)
                if (attendence.getCheckOutTime().toLocalTime().isAfter(standardCheckOutTime)) {
                    totalNightWorkMinutes += Duration.between(standardCheckOutTime, attendence.getCheckOutTime().toLocalTime()).toMinutes();
                }
            }
        }
        // 5. 기본급 계산 (시간당 10,000원 기준)
        double hourlyRate = 10000; // 시간당 급여
        double calculatedBaseSalary = Math.floor((totalWorkMinutes / 60.0) * hourlyRate);

        // 6. 공제 및 추가 항목 계산
        double lateDeductionRate = 100; // 지각/조퇴 시 분당 차감 금액
        double nightAllowanceRate = 200; // 야근 시 분당 추가 금액

        // 지각/조퇴 차감
        double deduction = Math.floor((totalLateMinutes + totalEarlyLeaveMinutes) * lateDeductionRate);

        // 야근 수당 추가
        double nightAllowance = Math.floor(totalNightWorkMinutes * nightAllowanceRate);

        // 4대보험과 세금 계산
        Map<String, Double> insuranceDetails = calculateInsurance(calculatedBaseSalary);

        // 최종 급여 계산
        double finalSalary = Math.floor(calculatedBaseSalary + nightAllowance - deduction) + request.getBonus();

        // 급여 등록 (기타 항목 설정)
        Payroll payroll = Payroll.builder()
                .user(user)
                .baseSalary(calculatedBaseSalary)
                .deductions(null)
                .overtimeHours(totalNightWorkMinutes)
                .bonus(request.getBonus())
                .totalAmount(finalSalary)
                .paymentStatus(false)
                .paymentDate(request.getPaymentDate())
                .nationPension(insuranceDetails.get("nationalPension"))
                .healthInsurance(insuranceDetails.get("healthInsurance"))
                .employmentInsurance(insuranceDetails.get("employmentInsurance"))
                .industrialAccidentInsurance(insuranceDetails.get("industrialAccidentInsurance"))
                .incomeTax(insuranceDetails.get("incomeTax"))
                .localTax(insuranceDetails.get("localTax"))
                .absentDeduction((int) deduction)
                .build();
        payrollRepository.save(payroll);

        // 7. DTO 생성 및 반환
        return PayrollIdDTO.builder()
                .payId(user.getUserId())  // 사용자 ID를 payId로 사용
                .totalLateMinutes(totalLateMinutes)
                .totalEarlyLeaveMinutes(totalEarlyLeaveMinutes)
                .totalWorkMinutes(totalWorkMinutes)
                .totalNightWorkMinutes(totalNightWorkMinutes)
                .baseSalary(calculatedBaseSalary)
                .deduction(deduction)
                .nightAllowance(nightAllowance)
                .finalSalary(finalSalary)
                .build();

    }
    // 기본급, 4대보험, 소득세, 주민세 계산
    private double calculateSalary(double baseSalary) {
        // 4대보험 계산 (고정 비율 예시)
        double nationalPension = baseSalary * 0.045; // 국민연금 4.5%
        double healthInsurance = baseSalary * 0.03335; // 건강보험 3.335%
        double employmentInsurance = baseSalary * 0.008; // 고용보험 0.8%
        double industrialAccidentInsurance = baseSalary * 0.007; // 산재보험 0.7% (사업주 부담)

        // 4대보험 총액 (근로자 부담 부분)
        double totalInsurance = nationalPension + healthInsurance + employmentInsurance;

        // 소득세 계산 (6% 고정)
        double incomeTax = baseSalary * 0.06;

        // 주민세 (소득세의 10%)
        double localTax = incomeTax * 0.10;

        // 총 공제액 (근로자 부담 4대보험, 소득세, 주민세)
        double totalDeductions = totalInsurance + incomeTax + localTax;

        // 최종 지급액 계산
        double finalSalary = baseSalary - totalDeductions;

        // 급여 명세서 출력
        System.out.println("급여 명세서:");
        System.out.println("기본급: " + baseSalary + "원");
        System.out.println("4대보험 (근로자 부담): ");
        System.out.println("  국민연금: " + nationalPension + "원");
        System.out.println("  건강보험: " + healthInsurance + "원");
        System.out.println("  고용보험: " + employmentInsurance + "원");
        System.out.println("소득세: " + incomeTax + "원");
        System.out.println("주민세: " + localTax + "원");
        System.out.println("산재보험 (사업주 부담): " + industrialAccidentInsurance + "원");
        System.out.println("총 공제액 (근로자 부담): " + totalDeductions + "원");
        System.out.println("최종 지급액: " + finalSalary + "원");

        return finalSalary;
    }

    // 최종 급여 계산을 위해 4대보험을 반환하도록 수정
    private Map<String, Double> calculateInsurance(double baseSalary) {
        double nationalPension = baseSalary * 0.045; // 국민연금 4.5%
        double healthInsurance = baseSalary * 0.03335; // 건강보험 3.335%
        double employmentInsurance = baseSalary * 0.008; // 고용보험 0.8%
        double industrialAccidentInsurance = baseSalary * 0.007; // 산재보험 0.7%

        // 4대보험 총액 (근로자 부담 부분)
        double totalInsurance = nationalPension + healthInsurance + employmentInsurance;

        // 소득세 계산 (6% 고정)
        double incomeTax = baseSalary * 0.06;

        // 주민세 (소득세의 10%)
        double localTax = incomeTax * 0.10;

        // 4대보험과 세금 정보를 Map에 담아서 반환
        Map<String, Double> insuranceDetails = new HashMap<>();
        insuranceDetails.put("nationalPension", nationalPension);
        insuranceDetails.put("healthInsurance", healthInsurance);
        insuranceDetails.put("employmentInsurance", employmentInsurance);
        insuranceDetails.put("industrialAccidentInsurance", industrialAccidentInsurance);
        insuranceDetails.put("incomeTax", incomeTax);
        insuranceDetails.put("localTax", localTax);
        insuranceDetails.put("totalInsurance", totalInsurance);

        return insuranceDetails;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updatePayrollStatus() {
        LocalDate today = LocalDate.now();
        payrollRepository.updatePaymentStatusForPastPayroll(today);

    }

    // 급여 전체 조회
    public ListForPaging getPayList(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Payroll> response = payrollRepository.findAll(pageable);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(0); // 소수점 자리수 제거
        numberFormat.setGroupingUsed(true);

        List<PersonalPayrollDTO> payrollDTO = response.getContent().stream()
                .map(payroll -> PersonalPayrollDTO.builder()
                        .payId(payroll.getPayId())
                        .connId(payroll.getUser().getUserName())
                        .username(payroll.getUser().getUserName())
                        .department(payroll.getUser().getDepartment())
                        .jobTitle(payroll.getUser().getJobTitle())
                        .baseSalary(numberFormat.format(payroll.getBaseSalary())) // 문자열 반환
                        .overtimePay(numberFormat.format((int) (payroll.getOvertimeHours() * 200)))
                        .bonus(numberFormat.format(payroll.getBonus()))
                        .incomeTax(numberFormat.format((int) payroll.getIncomeTax()))
                        .localIncomeTax(numberFormat.format((int) payroll.getLocalTax()))
                        .nationalPension(numberFormat.format((int) payroll.getNationPension()))
                        .healthInsurance(numberFormat.format((int) payroll.getHealthInsurance()))
                        .employmentInsurance(numberFormat.format((int) payroll.getEmploymentInsurance()))
                        .totalAmount(numberFormat.format((int) payroll.getTotalAmount()))
                        .absentDeduction(numberFormat.format((int) payroll.getAbsentDeduction()))
                        .paymentStatus(payroll.isPaymentStatus())
                        .build())
                .collect(Collectors.toList());


        return ListForPaging.builder()
                .items(Collections.singletonList(payrollDTO))
                .totalPages(response.getTotalPages())
                .totalElements(response.getTotalElements())
                .currentPage(page)
                .pageSize(size)
                .build();


    }
}
