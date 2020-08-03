package com.lcm.it;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
  小朋友老婆
**/
public class FileDirTreeShow {

    public static void main(String[] args) {

        List<FileDirTreeVO> list = new ArrayList<FileDirTreeVO>();
        String filePath = "D:\\fileTest\\1002\\22121";
        FileDirTreeVO parent=new FileDirTreeVO();
        parent.setMaxLevel(1);
        File file = new File(filePath);
        recursionFun(file, list, parent);

        System.out.println(list);

    }


    private static void recursionFun(File file, List<FileDirTreeVO> list, FileDirTreeVO parent) {

        FileDirTreeVO currentNode = new FileDirTreeVO();

        if (parent != null) {
            currentNode.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            currentNode.setpId(parent.getId());
            currentNode.setLevel(parent.getLevel() + 1);
        }
        currentNode.setLabel(file.getName());
        if (file.isDirectory()) {
            currentNode.setDirectory(true);
            File[] files=file.listFiles();
            for(File fileVo:files){
                recursionFun(fileVo,list,currentNode);
            }
        }
        if(parent!=null){
            list.add(currentNode);
        }

    }


}


class FileDirTreeVO {

    private String id;

    private String label;

    private String pId;

    private Boolean isDirectory = false;

    private Integer level = 0;

    private Integer maxLevel;


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public Boolean getDirectory() {
        return isDirectory;
    }

    public void setDirectory(Boolean directory) {
        isDirectory = directory;
    }

    @Override
    public String toString() {
        return "FileDirTreeVO{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", pId='" + pId + '\'' +
                ", isDirectory=" + isDirectory +
                ", level=" + level +
                ", maxLevel=" + maxLevel +
                '}';
    }
}