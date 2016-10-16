/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php\p
 */
package offishell.macro.lol;

/**
 * @version 2016/10/05 16:59:32
 */
public class Xin extends LoLMacro {

    /**
     * 
     */
    private Xin() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeCastTime(Skill skill) {
        switch (skill) {
        case AA:
            return 310;
        }
        return super.computeCastTime(skill);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void combo() {
        cast(Skill.Move);
        cast(Skill.Item6);

        if (cast(Skill.E)) {
            cast(Skill.R);
            cast(Skill.SS2);
            cast(Skill.W);
            cast(Skill.AA);
            cast(Skill.Q);
            cast(Skill.AA);
            cast(Skill.Item2);
        }
    }

    /**
     * 
     */
    public static void main(String[] args) {
        LoLMacro.active();
    }
}
