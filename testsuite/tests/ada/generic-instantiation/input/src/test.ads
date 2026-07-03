with Gen;
with Top_Level_Inst;
with Transitive_Top_Level;
with Proc;

package Test is

    package Inst is new Gen (1, Integer, Proc.P1);
    package Subgen is new Inst.Subgen (Integer);

    generic
       type T is range <>;
    package Transit is
        package Subgen is new Top_Level_Inst.Subgen (T);
    end Transit;

    package Transitive1 is new Transit (Integer);
    package Transitive2 is new Transitive_Top_Level (Integer);

    function Foo return Test.Transitive2.Subgen.Rec is (I => 1);

end Test;
