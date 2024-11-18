package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;

import java.util.Objects;

public class Comands {
    private int playerIdx;
    private Object object;
    private String comand;

    public Comands(int playerIdx, String comand, Object object) {
        this.comand = comand;
        this.object = object;
        this.playerIdx = playerIdx;
    }

    public Comands(String comand, Object object){
        this.comand = comand;
        this.object = object;
    }

    public void playerOutput(ArrayNode output){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", comand);
        objectNode.put("playerIdx", playerIdx);
        objectNode.putPOJO("output", object);
        output.addPOJO(objectNode);
    }
    public void easyCommand(ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", comand);
        objectNode.putPOJO("output", object);
        output.addPOJO(objectNode);
    }
    public void errorCommand(ArrayNode output){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", comand);
        objectNode.putPOJO("output", object);
        output.addPOJO(objectNode);
    }
}
