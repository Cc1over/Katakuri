package com.hebaiyi.www.katakuri.engine.InnerImageEngine;

import com.hebaiyi.www.katakuri.engine.ImageEngine;

public class InnerEngine implements ImageEngine {

    private InnerEngine(){

    }

    private static class Singleton{
        private static final InnerEngine instance = new InnerEngine();
    }

    public static InnerEngine getInstance(){
        return Singleton.instance;
    }


}
