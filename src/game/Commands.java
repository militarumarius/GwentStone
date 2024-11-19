package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Commands {
    private int playerIdx;
    private Object object;
    private Object objectTwo;
    private Object objectThree;
    private String principalComand;
    private String comandTwo;
    private String comand;
    private int idx;
    private String error;

    public Commands(final int playerIdx, final String comand, final Object object) {
        this.principalComand = comand;
        this.object = object;
        this.playerIdx = playerIdx;
    }

    public Commands(final String comand, final Object object) {
        this.principalComand = comand;
        this.object = object;
    }

    public Commands(final String comand, final  int idx, final String error) {
        this.principalComand = comand;
        this.idx = idx;
        this.error = error;
    }

    public Commands(final String comand, final String comandTwo,
                    final Object object, final String error) {
        this.comand = comandTwo;
        this.principalComand = comand;
        this.object = object;
        this.error = error;
    }

    public Commands(final String principalComand, final String comand, final String comandTwo,
                    final Object object, final Object objectTwo, final String error) {
        this.comandTwo = comandTwo;
        this.comand = comand;
        this.principalComand = principalComand;
        this.object = object;
        this.objectTwo = objectTwo;
        this.error = error;
    }

    public Commands(final String principalComand, final String comand, final String comandTwo,
                    final Object object, final Object objectTwo, final Object objectThree) {
        this.comandTwo = comandTwo;
        this.comand = comand;
        this.principalComand = principalComand;
        this.object = object;
        this.objectTwo = objectTwo;
        this.objectThree = objectThree;
    }
    /**
     * method that make an JSON object for getPlayerIdx command
     * @param output array node to display the object
     */
    public void playerOutput(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.put("playerIdx", playerIdx);
        objectNode.putPOJO("output", object);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for place card command
     * @param output array node to display the object
     */
    public void errorPlaceCard(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "placeCard");
        objectNode.put("handIdx", idx);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for statistics commands
     * @param output array node to display the object
     */
    public void easyCommand(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.putPOJO("output", object);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for use Hero ability command
     * @param output array node to display the object
     */
    public void errorCommandHero(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.put("affectedRow", idx);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for attack hero command
     * @param output array node to display the object
     */
    public void errorAttackHero(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.putPOJO(comand, object);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for card use ability command
     * @param output array node to display the object
     */
    public void errorUseAbility(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.putPOJO(comand, object);
        objectNode.putPOJO(comandTwo, objectTwo);
        objectNode.put("error", error);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for place card command error
     */
    public void errorPlaceCardCommand(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.putPOJO(comand, object);
        objectNode.putPOJO(comandTwo, objectTwo);
        objectNode.put("output", error);
        output.addPOJO(objectNode);
    }

    /**
     * method that make an JSON object for place card command
     */
    public void placeCardCommand(final ArrayNode output) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", principalComand);
        objectNode.putPOJO(comand, object);
        objectNode.putPOJO(comandTwo, objectTwo);
        objectNode.putPOJO("output", objectThree);
        output.addPOJO(objectNode);
    }
}
