package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.TwinState;
import com.happiesthealth.digitaltwin.service.TwinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{id}/twin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TwinController {
    private final TwinService twinService;

    @GetMapping
    public ResponseEntity<TwinState> getCurrentTwin(@PathVariable Long id) {
        return twinService.getCurrentTwin(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(twinService.recalculateTwinState(id)));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TwinState>> getTwinHistory(@PathVariable Long id) {
        return ResponseEntity.ok(twinService.getTwinHistory(id));
    }

    @PostMapping("/recalculate")
    public ResponseEntity<TwinState> recalculateTwin(@PathVariable Long id) {
        return ResponseEntity.ok(twinService.recalculateTwinState(id));
    }
}
