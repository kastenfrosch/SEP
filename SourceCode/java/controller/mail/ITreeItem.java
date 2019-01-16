package controller.mail;

import java.util.List;

public interface ITreeItem {

    String getEmail();

    List<ITreeItem> getChildren();

}
