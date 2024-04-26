package com.ssafy.stellar.star.controller;


import com.ssafy.stellar.star.dto.response.StarReturnAllDto;
import com.ssafy.stellar.star.entity.StarEntity;
import com.ssafy.stellar.star.service.StarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Star", description = "별 관련 API")
@RestController
@Slf4j
@RequestMapping("/star")
public class StarController {

    private final StarService starService;

    public StarController(StarService starService) {
        this.starService = starService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> returnAllStar() {
        try {
            List<StarReturnAllDto> list = starService.returnAllStar();

            return new ResponseEntity<List<StarReturnAllDto>>(list, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
