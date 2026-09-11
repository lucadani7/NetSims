package com.lucadani.netsims.simulation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    @NotBlank(message = "Node ID is mandatory")
    private String id;

    @NotBlank(message = "Node name is mandatory")
    private String name;

    @Min(value = 1, message = "Bandwidth must be at least 1 bps")
    private int bandwidthBps;

    private List<String> connectedNodeIds = new ArrayList<>();
}
