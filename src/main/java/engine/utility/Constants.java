package engine.utility;

public class Constants {
    private static Constants instance = null;

    public static final float gridSize = 32.0f;

    private Constants(){}

    public Constants get(){
        if(Constants.instance == null){
            Constants.instance = new Constants();
        }

        return Constants.instance;
    }
}
