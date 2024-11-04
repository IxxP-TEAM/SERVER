package com.ip.api.service;

import com.ip.api.domain.User;
import com.ip.api.dto.user.UserResponse.ListForPaging;
import com.ip.api.dto.user.UserResponse.UserDTO;
import com.ip.api.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HRService {
    private final UserRepository userRepository;

    public ListForPaging getUserList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> response = userRepository.findAll(pageable);

        // User 엔티티를 UserDTO로 변환
        List<UserDTO> userDTOList = response.getContent().stream()
                .map(userEntity -> new UserDTO(
                        userEntity.getUserId(),
                        userEntity.getUserName(),
                        userEntity.getEmail(),
                        userEntity.getDepartment(),
                        userEntity.getJobTitle(),
                        userEntity.getUserPhone(),
                        userEntity.getUserStatus()
                ))
                .collect(Collectors.toList());

        // ListForPaging 객체 생성
        return ListForPaging.<UserDTO>builder()
                .totalElements(response.getTotalElements())
                .totalPages(response.getTotalPages())
                .pageSize(response.getSize())
                .currentPage(response.getNumber())
                .items(userDTOList)
                .build();
    }
}
