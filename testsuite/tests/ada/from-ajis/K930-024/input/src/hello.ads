with Ada.Containers.Indefinite_Vectors;
with Ada.Containers.Vectors;

package Hello is


   procedure Say (Something : String);

   type Combo_Switch is tagged record
     Line      : Positive := 1;
   end record;

   type Switch_Description is tagged record
     Line      : Positive := 1;
   end record;

   package Combo_Switch_Vectors is new
     Ada.Containers.Vectors (Natural, Combo_Switch);

   package Switch_Description_Vectors is new
     Ada.Containers.Indefinite_Vectors (Natural, Switch_Description);

   type Switches_Editor_Config_Record is tagged record
       Entries           : Combo_Switch_Vectors.Vector;
       Switches          : Switch_Description_Vectors.Vector;
   end record;


end Hello;
