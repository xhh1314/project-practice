package com.example.springcloud.icbc.controller;


import com.example.springcloud.bankofchina.manage.Restful;
import com.example.springcloud.icbc.entity.BalanceDO;
import com.example.springcloud.icbc.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 账户金额操作接口
 */
@Controller
@ResponseBody
@RequestMapping(value = "/balance")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    /**
     * 金额转出服务
     * @param balanceDO
     * @return
     */
    @RequestMapping(value = "/decrease",method = RequestMethod.PUT)
    public Restful decreaseNumber(@RequestBody BalanceDO balanceDO){
        if(balanceDO==null)
            return Restful.failure("账户信息缺失！");
        if(balanceDO.getAmount()==0 || balanceDO.getAmount()==null)
            return Restful.failure("金额不能为空!");
        if(balanceDO.getBalanceId()==null){
            return Restful.failure("账户id不能为空！");
        }
        return balanceService.decreaseAmount(balanceDO.getBalanceId(),balanceDO.getAmount());
    }

    /**
     * 金额转入服务
     * @param balanceDO
     * @return
     */
    @RequestMapping(value = "/increase",method = RequestMethod.PUT)
    public Restful increaseNumber(@RequestBody BalanceDO balanceDO){
        if(balanceDO==null)
            return Restful.failure("账户信息缺失！");
        if(balanceDO.getAmount()==0 || balanceDO.getAmount()==null)
            return Restful.failure("金额不能为空!");
        if(balanceDO.getBalanceId()==null){
            return Restful.failure("账户id不能为空！");
        }
        return balanceService.increaseAmount(balanceDO.getBalanceId(),balanceDO.getAmount());
    }


}