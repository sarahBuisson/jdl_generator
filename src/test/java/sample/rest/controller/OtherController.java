package sample.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sample.rest.SomeClassDTO;

@RestController("other")
public class OtherController {

    @GetMapping("get")
    public SomeClassDTO getSome(@RequestParam(required = false) String param1) {

        return new SomeClassDTO();
    }

    @PostMapping("set")
    public SomeClassDTO setSome(SomeClassDTO request) {
        return new SomeClassDTO();
    }


    public SomeClassDTO doSome(SomeClassDTO request) {
        return null;
    }
}
