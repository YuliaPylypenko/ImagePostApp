package com.example.BlogWebSite.repo;

import com.example.BlogWebSite.model.Post;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;



@Repository
@AllArgsConstructor
public class PostSearchRepo {

    private EntityManager entityManager;

    public SearchResult<Post> searchByTitle(String searchText, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession.search(Post.class)
                .where(f -> f.match().field("title").matching(searchText))
                .fetch(pageable.getPageSize());
    }

}
