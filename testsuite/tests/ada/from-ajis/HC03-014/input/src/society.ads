--::::::::::
--society.ads
--::::::::::
package Society is

  -- Dining Philosophers - Ada 95 edition
  -- Society gives unique ID's to people, and registers their names
  -- Michael B. Feldman, The George Washington University,
  -- July, 1995.

   subtype Unique_DNA_Codes is Positive range 1..5;

   subtype Name is String (1 .. 18);

   type Name_Types is array (Unique_DNA_Codes) of Name;

   A_Name : Name;

   Name_Register : Name_Types :=
     ("Edsger Dijkstra   ",
      "Bjarne Stroustrup ",
      "Chris Anderson    ",
      "Tucker Taft       ",
      "Jean Ichbiah      ");

   type Rec is record
      F : Name;
   end record;

end Society;
