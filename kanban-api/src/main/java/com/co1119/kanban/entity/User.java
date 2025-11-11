package com.co1119.kanban.entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
// `user` はデータベースで予約語となる可能性があるため、
// テーブル名を `users` に変更して衝突を回避します。
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(
            // Boardエンティティ側の'user'フィールドとマッピング
            mappedBy = "user",
            // ユーザ削除時にボードも削除
            cascade = CascadeType.ALL,
            // ユーザからボードが削除されたらDBからも削除
            orphanRemoval = true

    )
    private Set<Board> boards;

    /**
     * 新規追加用のコンストラクタです。
     * 
     * @param email
     * @param password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
