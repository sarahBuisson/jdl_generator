package sample.rest.controller;

import org.springframework.web.bind.annotation.*;
import sample.rest.SomeClassDTO;

@RestController("other")
public class OtherController {

    @GetMapping("get")
    public SomeClassDTO getSome(@RequestParam(required = false) String param1) {

        return new SomeClassDTO();
    }

    @PostMapping("set")
    public SomeClassDTO setSome(@RequestBody() SomeClassDTO request) {
        return new SomeClassDTO();
    }

}
