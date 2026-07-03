with Top_Level_Inst;

generic
   type T is range <>;
package Transitive_Top_Level is
    package Subgen is new Top_Level_Inst.Subgen (T);
end Transitive_Top_Level;
