package com.example.b03.repository.search;

import com.example.b03.domain.Post;
import com.example.b03.domain.QPost;
import com.example.b03.dto.PageRequestDTO;
import com.example.b03.dto.PageResponseDTO;
import com.example.b03.dto.PostDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {

    public PostSearchImpl() {
        super(Post.class);
    }

    @Override
    public PageResponseDTO<PostDTO> search(PageRequestDTO requestDTO, Integer categoryId) {
        QPost post = QPost.post;

        Pageable pageable = requestDTO.getPageable("postId");

        JPQLQuery<Post> query = from(post);

        // 검색 조건
        if (requestDTO.getTypes() != null && requestDTO.getKeyword() != null) {
            String keyword = requestDTO.getKeyword();
            BooleanBuilder builder = new BooleanBuilder();

            for (String type : requestDTO.getTypes()) {
                switch (type) {
                    case "t" -> builder.or(post.title.containsIgnoreCase(keyword));
                    case "d" -> builder.or(post.description.containsIgnoreCase(keyword));
                    case "c" -> builder.or(post.company.companyName.containsIgnoreCase(keyword));
                }
            }

            query.where(builder);
        }

        query.where(post.postId.gt(0));
        getQuerydsl().applyPagination(pageable, query);

        List<Post> resultList = query.fetch();
        long totalCount = query.fetchCount();

        List<PostDTO> dtoList = resultList.stream().map(this::toDTO).collect(Collectors.toList());

        return PageResponseDTO.<PostDTO>withAll()
                .pageRequestDTO(requestDTO)
                .dtoList(dtoList)
                .total((int) totalCount)
                .build();
    }

    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .memberNo(post.getCompany().getMember().getMemberNo())
                .companyName(post.getCompany().getCompanyName())
                .address(post.getAddress())
                .companyPhone(post.getCompany().getMember().getPhone())
                .title(post.getTitle())
                .description(post.getDescription())
                .salary(post.getSalary())
                .deadline(post.getDeadline())
                .postedDate(post.getPostedDate())
                .updatedDate(post.getUpdatedDate())
                .createdAt(post.getCreatedAt())
                .updatedAtEntity(post.getUpdatedAt())
                .isDeleted(post.getIsDeleted())
                .build();
    }
}
