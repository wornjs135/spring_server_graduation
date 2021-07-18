package inu.graduation.sns.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String storeName;

    private String imageUrl;

    private String thumbnailImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Image createImage(String imageUrl, String thumbnailImageUrl, String storeName, Post post) {
        Image image = new Image();
        image.imageUrl = imageUrl;
        image.thumbnailImageUrl = thumbnailImageUrl;
        image.storeName = storeName;
        image.post = post;
        return image;
    }
}
