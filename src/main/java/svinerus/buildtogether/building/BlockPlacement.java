package svinerus.buildtogether.building;

public enum BlockPlacement {
    CORRECT,  // set correct block
    REMOVE_INCORRECT, // break incorrect block
    INCORRECT,  // place or break incorrect block,
    OUTSIDE_LAYER;

    public boolean isCorrect() {
        return this == CORRECT || this == REMOVE_INCORRECT;
    }
}
