package engine;

public class Project {

    private String projectLocation;
    private String assetDirectoryLocation;
    private GameWindow window = GameWindow.get();

    public Project(){
        // TODO handle empty constructor
        this.window = GameWindow.get();
    }

    public Project(String assetDirectoryLocation){
        this.assetDirectoryLocation = assetDirectoryLocation;
    }

    public void setAssetDirectoryLocation(String assetDirectoryLocation){
        this.assetDirectoryLocation = assetDirectoryLocation;
    }

    public String getAssetDirectoryLocation(){
        return this.assetDirectoryLocation;
    }
}
