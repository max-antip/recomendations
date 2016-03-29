package kz.viaphone.research.domain;


public class CategoryImpl implements Category {


    private String code;
    private String title;



    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
