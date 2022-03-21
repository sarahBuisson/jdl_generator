package sample.rest;

import org.springframework.web.bind.annotation.*;
import sample.rest.SomeClassDTO;

@RestController("some")
public class SomeController {

    @GetMapping("get")
    public SomeClassDTO getSome(@RequestParam(required = false) String param1) {

        return new SomeClassDTO();
    }

    @PostMapping("set")
    public SomeClassDTO setSome(@RequestBody SomeClassDTO request) {
        return new SomeClassDTO();
    }


    public SomeClassDTO doSome(@RequestBody SomeClassDTO request) {
        return null;
    }
}
