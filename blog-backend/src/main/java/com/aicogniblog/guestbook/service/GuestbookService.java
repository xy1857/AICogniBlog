package com.aicogniblog.guestbook.service;

import com.aicogniblog.common.result.PageResult;
import com.aicogniblog.guestbook.dto.GuestbookRequest;
import com.aicogniblog.guestbook.dto.GuestbookVO;
import com.aicogniblog.guestbook.entity.Guestbook;
import com.aicogniblog.guestbook.mapper.GuestbookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 留言板服务
 */
@Service
@RequiredArgsConstructor
public class GuestbookService {
    
    private final GuestbookMapper guestbookMapper;
    
    /**
     * 获取留言列表（分页）
     */
    public PageResult<GuestbookVO> listMessages(int page, int size) {
        int offset = (page - 1) * size;
        List<Guestbook> messages = guestbookMapper.selectPage(offset, size);
        int total = guestbookMapper.count();
        
        List<GuestbookVO> voList = messages.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        
        // 修改：手动创建 PageResult 对象而不是使用 of 方法
        PageResult<GuestbookVO> result = new PageResult<>();
        result.setRecords(voList);
        result.setTotal(total);
        result.setCurrent(page);
        result.setPages((total + size - 1) / size); // 计算总页数
        return result;
    }
    
    /**
     * 发表留言
     */
    public Long createMessage(GuestbookRequest request, Long userId, String ipAddress) {
        Guestbook guestbook = new Guestbook();
        guestbook.setUserId(userId);
        guestbook.setName(request.getName());
        guestbook.setEmail(request.getEmail());
        guestbook.setContent(request.getContent());
        guestbook.setIpAddress(ipAddress);
        guestbook.setStatus(1); // 直接发布，不需要审核
        
        // 生成头像 URL
        if (userId != null) {
            guestbook.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + userId);
        } else {
            guestbook.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getName());
        }
        
        guestbookMapper.insert(guestbook);
        return guestbook.getId();
    }
    
    /**
     * 删除留言
     */
    public void deleteMessage(Long id) {
        guestbookMapper.deleteById(id);
    }
    
    /**
     * 转换为 VO
     */
    private GuestbookVO toVO(Guestbook guestbook) {
        GuestbookVO vo = new GuestbookVO();
        BeanUtils.copyProperties(guestbook, vo);
        return vo;
    }
}

