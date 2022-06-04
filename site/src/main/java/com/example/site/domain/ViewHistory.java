package com.example.site.domain;

import com.example.site.util.BooksCounterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NamedNativeQuery;

import javax.persistence.*;

@Entity
@Table(name = "view_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedNativeQuery(name = "ViewHistory.getOthersPrefferedBooks",
                  query = "SELECT v.book_id, sum(v.counter) FROM View_History v WHERE v.user_id in ?1 and v.book_id not in ?2 GROUP BY v.book_id",
                  resultSetMapping = "Mapping.BooksCounterDTO")
@SqlResultSetMapping(name = "Mapping.BooksCounterDTO",
                     classes = @ConstructorResult(targetClass = BooksCounterDTO.class,
                                                  columns = {@ColumnResult(name = "book_id", type=Long.class),
                                                          @ColumnResult(name = "sum(v.counter)", type=Integer.class)}))
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Books book;

    @Column(name = "counter")
    private int counter;
}
