package tw.housemart.test.retrofit.together;

/**
 * Created by user on 2016/12/16.
 */

public interface ChangeListener {
    public void onJoin(InfoObject obj);
    public void onLeave(InfoObject obj);
    public void onLocate(InfoObject obj);
    public void onNetLosed();
}
