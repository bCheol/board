package com.example.board.service;

import com.example.board.config.AesUtil;
import com.example.board.dto.*;
import com.example.board.entity.AccountEntity;
import com.example.board.entity.AccountTempEntity;
import com.example.board.repository.AccountRepository;
import com.example.board.repository.AccountTempRepository;
import com.example.board.repository.AdminAccountSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    public static boolean[] cancel = {false, false, false, false};

    private final AccountRepository accountRepository;
    private final AccountTempRepository accountTempRepository;
    private final AesUtil aesUtil;
    private final JdbcTemplate jdbcTemplate;

    public ResponseListResult<AdminMemberRow> listMembers(AdminSearchCondition cond) {

        Specification<AccountEntity> spec = Specification.where(null);

        if ("admin".equals(cond.getRoleType())) {
            spec = spec.and(AdminAccountSpecification.roleLike("ADMIN"));
        } else if ("user".equals(cond.getRoleType())) {
            spec = spec.and(AdminAccountSpecification.roleEquals("USER"));
        }
        if (StringUtils.hasText(cond.getExistYn())) {
           spec = spec.and(AdminAccountSpecification.existEquals(Integer.valueOf(cond.getExistYn())));
        }
        if (StringUtils.hasText(cond.getBlockYn())) {
            spec = spec.and(AdminAccountSpecification.blockEquals(Integer.valueOf(cond.getBlockYn())));
        }
        if (StringUtils.hasText(cond.getKeyword())) {
            spec = spec.and(AdminAccountSpecification.accountIdLike(cond.getKeyword()));
        }

        Pageable pageable = PageRequest.of(cond.getPage() - 1, cond.getSize(), Sort.by(Sort.Direction.ASC, "accountId"));
        Page<AdminMemberListView> page = accountRepository.findBy(spec, query -> query.as(AdminMemberListView.class).page(pageable));

        List<AdminMemberRow> changeTypeList = roleChangeType(page.getContent());
        PageInfo pageInfo = new PageInfo(cond.getPage(), cond.getSize(), page.getTotalElements());
        return new ResponseListResult<AdminMemberRow>(changeTypeList, pageInfo);
    }

    private List<AdminMemberRow> roleChangeType(List<AdminMemberListView> members) {

        if (members.isEmpty()) {
            return List.of();
        }

        List<AdminMemberRow> list = new ArrayList<>();
        for (AdminMemberListView member : members) {
            if (member.getRole().contains("ADMIN")) {
                list.add(new AdminMemberRow(member, "관리자"));
            } else {
                list.add(new AdminMemberRow(member, "일반"));
            }
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public int changeBlock(String accountId, int value) {

        AccountEntity accountEntity = accountRepository.findByAccountIdAndExist(accountId, 1);
        if (accountEntity != null) {
            accountEntity.setBlock(value);
            return 1;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public int changeMemberRole(String accountId, String role) {

        AccountEntity accountEntity = accountRepository.findByAccountIdAndExist(accountId, 1);

        if (accountEntity != null) {
            if (accountEntity.getBlock() == 1) {
                return -1;
            }
            accountEntity.setRole(role);
            return 1;
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public void copyTable() {

        cancel[0] = false;

        List<AccountEntity> en_list = accountRepository.findAccountInfo();

        String insertSQL = "Insert Into account_info_temp (account_id, name, phone) Values (?, ?, ?)";
        jdbcTemplate.batchUpdate(insertSQL, en_list, 100, (ps, post) -> {
            if (cancel[0]) {
                throw new RuntimeException();
            }
            ps.setString(1, post.getAccountId());
            ps.setString(2, post.getName());
            ps.setString(3, post.getPhone());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void tempTableChange(String previousKey) {

        cancel[1] = false;

        List<AccountTempEntity> tempList = accountTempRepository.findAll();

        String updateSQL = "Update account_info_temp set name=?, phone=? where account_id=?";
        jdbcTemplate.batchUpdate(updateSQL, tempList, 100, (ps, post) -> {
            if (cancel[1]) {
                throw new RuntimeException();
            }
            String name = aesUtil.previousDecrypt(previousKey, post.getName());
            String phone = aesUtil.previousDecrypt(previousKey, post.getPhone());

            ps.setString(1, aesUtil.encrypt(name));
            ps.setString(2, aesUtil.encrypt(phone));
            ps.setString(3, post.getAccountId());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void orgTableChange() {

        cancel[2] = false;

        List<AccountTempEntity> tempList = accountTempRepository.findAll();

        String updateSQL = "Update account_info set name=?, phone=? Where account_id=?";
        jdbcTemplate.batchUpdate(updateSQL, tempList, 100, (ps, post) -> {
            if (cancel[2]) {
                throw new RuntimeException();
            }
            ps.setString(1, post.getName());
            ps.setString(2, post.getPhone());
            ps.setString(3, post.getAccountId());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTempTable() {

        cancel[3] = false;

        accountTempRepository.deleteAllInBatch();

        if (cancel[3]) {
            throw new RuntimeException();
        }
    }

}