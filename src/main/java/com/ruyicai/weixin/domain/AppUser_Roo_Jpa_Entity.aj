// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.weixin.domain;

import com.ruyicai.weixin.domain.AppUser;
import javax.persistence.Entity;
import javax.persistence.Table;

privileged aspect AppUser_Roo_Jpa_Entity {
    
    declare @type: AppUser: @Entity;
    
    declare @type: AppUser: @Table(name = "appuser");
    
}
