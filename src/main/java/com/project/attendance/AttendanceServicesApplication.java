//package com.project.attendance;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//
//@SpringBootApplication
//@EnableFeignClients
//
//public class AttendanceServicesApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(AttendanceServicesApplication.class, args);
//    }
//}
//package com.project.attendance;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//
//@SpringBootApplication(
//    excludeName = {
//        "org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration"
//    }
//)
//@EnableFeignClients
//public class AttendanceServicesApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(AttendanceServicesApplication.class, args);
//    }
//}
package com.project.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class AttendanceServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceServicesApplication.class, args);
    }
}


