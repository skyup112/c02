package com.example.b03.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type;

    private String keyword;

    private String link;

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String... props) {
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
//         bno, title 두개를 내림차순으로 정렬 단,bno가 최우선 순위로 내림차순 진행
//        Sort sort = Sort.by(
//                Arrays.stream(props)
//                        .map(Sort.Order::desc)
//                        .toList()       //Sort 객체 만듬
//        );
//        return PageRequest.of(this.page - 1, this.size, sort);
    }


    public String getLink() {
        if (link == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page" + this.page);
            builder.append("&size" + this.size);
            if (type != null && type.length() > 0) {
                builder.append("&type" + type);

            }

            if (keyword != null) {
                try {
                    builder.append("&keyword" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
            link = builder.toString();
        }
        return link;
    }

}
