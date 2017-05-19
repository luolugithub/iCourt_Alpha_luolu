package com.icourt.alpha.entity.event;

import com.icourt.alpha.entity.bean.CustomerEntity;

/**
 * Description  更新客户信息event
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class UpdateCustomerEvent {

    public CustomerEntity customerEntity;

    public UpdateCustomerEvent(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }
}
