package com.shop.userhold;

import com.shop.entity.User;

public class UserHold {
   public static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
   public static void  setUser(Integer id){
       threadLocal.set(id);
   }
   public static Integer getUser(){
       return threadLocal.get();
   }
   public static void removeUser(){
       threadLocal.remove();
   }
}
