package com.fpts.cockpitInterface;

import com.fpts.common.core.page.TableDataInfo;
import com.fpts.framework.web.domain.server.Sys;
import com.fpts.system.domain.Certification;
import com.fpts.system.domain.SysNotice;
import com.fpts.system.service.ICertificationService;
import com.fpts.system.service.ISysNoticeService;
import com.fpts.system.service.impl.CertificationServiceImpl;
import com.fpts.todo.domain.TodoList;
import com.fpts.todo.service.ITodoListService;
import com.fpts.finance_forum.domain.FinanceForum;
import com.fpts.finance_forum.service.IFinanceForumService;
import com.fpts.system.domain.SysUserOnline;
import com.fpts.system.service.ISysUserOnlineService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 可视化大屏幕Controller
 *
 * @author Guoxi Zhang & Yicheng Li & Chenglong Yu
 * @date 2022-12-09
 */
@Controller
@RequestMapping("/data")
public class CockpitInterfaceController {
    private String prefix = "data";

    @Autowired
    private ITodoListService todoListService;
    @Autowired
    private IFinanceForumService financeForumService;
    @Autowired
    private  ISysUserOnlineService sysUserOnlineService;
    @Autowired
    private ISysNoticeService noticeService;
    @Autowired
    ICertificationService certificationService;

    @GetMapping()
    public String cockpitInterface(ModelMap mmap, TodoList todoList, FinanceForum financeForum, SysUserOnline sysUserOnline, SysNotice notice)
    {
        /**
         * 查询待办事项列表和金融论坛
         */
        List<TodoList> list = todoListService.selectTodoListList(todoList);
        for(TodoList t:list){
            String detail = t.getDetail();
            System.out.println(detail);
            if(detail.contains("<p>")){
                t.setDetail(t.getDetail().replace("<p>", ""));
                System.out.println(detail.replace("<p>", ""));
            }
            if(detail.contains("</p>")){
                t.setDetail(t.getDetail().replace("</p>", ""));
            }
        }
        mmap.put("todoList", list);
        List<FinanceForum> list1 = financeForumService.selectFinanceForumList(financeForum);
        for(FinanceForum f:list1){
            String content = f.getContent();
            if(content.contains("<p>")){
                f.setContent(f.getContent().replace("<p>", ""));
            }
            if(content.contains("</p>")){
                f.setContent(f.getContent().replace("</p>", ""));
            }
        }
        mmap.put("financeForum", list1);
        List<SysUserOnline> list2 = sysUserOnlineService.selectUserOnlineList(sysUserOnline);
        mmap.put("sysUserOnline", list2);
        List<SysNotice> list3 = noticeService.selectNoticeList(notice);
        mmap.put("notice", list3);

        chartList(mmap);
        //返回视图
        return prefix + "/cockpitInterface";
    }

    public void chartList(ModelMap mmap) {
        List<Certification> list = certificationService.selectCertificationList(new Certification());
        Map<String, Integer> map = new TreeMap<String, Integer>();
        String done = "1";
        String undone = "2";
        map.put(done, 0);
        map.put(undone, 0);
        for (Certification c : list) {
            String realName = c.getRealName();
            String identityCardNumber = c.getIdentityCardNumber();
            if ((!realName.isEmpty()) && (!identityCardNumber.isEmpty())) {
                int cnt = map.get(done);
                map.replace(done, cnt, cnt + 1);
            } else {
                int cnt = map.get(undone);
                map.replace(undone, cnt, cnt + 1);
            }
        }
        List<Map.Entry<String, Integer>> list1 = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        List<String> statusList = new ArrayList<String>();
        List<Integer> countList = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> m : list1) {
            statusList.add(m.getKey());
            countList.add(m.getValue());
        }
        System.out.println(statusList.toString());
        System.out.println(countList.toString());
        mmap.put("statusList", statusList);
        mmap.put("countList", countList);
    }
}
