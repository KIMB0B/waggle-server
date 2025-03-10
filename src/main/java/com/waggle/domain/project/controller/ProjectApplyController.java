package com.waggle.domain.project.controller;

import com.waggle.domain.project.dto.ProjectResponseDto;
import com.waggle.domain.project.service.ProjectService;
import com.waggle.domain.user.dto.UserResponseDto;
import com.waggle.global.response.ApiStatus;
import com.waggle.global.response.BaseResponse;
import com.waggle.global.response.ErrorResponse;
import com.waggle.global.response.SuccessResponse;
import com.waggle.global.response.swagger.ProjectSuccessResponse;
import com.waggle.global.response.swagger.ProjectsSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "프로젝트 지원", description = "프로젝트 지원 관련 API")
@RestController
@RequestMapping("/project/apply")
@RequiredArgsConstructor
public class ProjectApplyController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 지원자 조회",
            description = "프로젝트에 지원한 사용자들을 조회한다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 지원자 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProjectSuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 모집글이 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<Set<UserResponseDto>>> fetchAppliedUsers(@PathVariable String projectId, @PathVariable String test) {
        return SuccessResponse.of(ApiStatus._OK, projectService.getAppliedUsersByProjectId(UUID.fromString(projectId)).stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @PutMapping("/{projectId}/approve/{userId}")
    @Operation(
            summary = "프로젝트 모집글 참여자 승인",
            description = "프로젝트 모집글에 참여한 사용자를 승인한다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 모집글 참여자 승인 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProjectSuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한이 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 모집글 혹은 사용자가 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<Set<UserResponseDto>>> approveUser(@PathVariable String projectId, @PathVariable String userId) {
        return SuccessResponse.of(ApiStatus._OK, projectService.approveAppliedUser(UUID.fromString(projectId), userId).stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @PutMapping("/{projectId}/reject/{userId}")
    @Operation(
            summary = "프로젝트 모집글 참여자 거절",
            description = "프로젝트 모집글에 참여한 사용자를 거절한다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 모집글 참여자 거절 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProjectSuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한이 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 모집글 혹은 사용자가 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<Set<UserResponseDto>>> rejectUser(@PathVariable String projectId, @PathVariable String userId) {
        return SuccessResponse.of(ApiStatus._OK, projectService.rejectAppliedUser(UUID.fromString(projectId), userId).stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    @GetMapping("/who/me")
    @Operation(
            summary = "내가 지원한 프로젝트 조회",
            description = "현재 로그인 된 사용자가 지원한 프로젝트를 조회합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 수정 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProjectsSuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<Set<ProjectResponseDto>>> getAppliedProjects() {
        Set<ProjectResponseDto> appliedProjects = projectService.getAppliedProjects().stream()
                .map(ProjectResponseDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return SuccessResponse.of(ApiStatus._OK, appliedProjects);
    }

    @PostMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 지원",
            description = "프로젝트에 지원합니다. 성공 시 지원한 프로젝트 정보를 반환합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "프로젝트 지원 성공",
                    content = @Content(
                            schema = @Schema(implementation = ProjectSuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<ProjectResponseDto>> applyProject(@PathVariable String projectId) {
        ProjectResponseDto applyProject = ProjectResponseDto.from(projectService.applyProject(projectId));
        return SuccessResponse.of(ApiStatus._CREATED, applyProject);
    }

    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 지원 취소",
            description = "프로젝트 지원을 취소합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "프로젝트 지원 취소 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<BaseResponse<Object>> cancelApplyProject(@PathVariable String projectId) {
        projectService.cancelApplyProject(projectId);
        return SuccessResponse.of(ApiStatus._NO_CONTENT, null);
    }
}
