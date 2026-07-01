package Animals is

   -- Abstract type
   type Animal is abstract tagged null record;

   procedure Shout (A : Animal) is abstract;

   -- Enum type
   type Color is (Red, Green, Blue);

   function Image (C: Color) return String is (C'Image);

   -- Tagged type
   type Parrot is new Animal with record
      C : Color;
   end record;

   procedure Shout (A: Parrot);

   procedure Repeat (A: Parrot; S: String);

   procedure Call_Shout (A: Animal'Class);

   -- Access type
   type Parrot_Acc is access all Parrot;

   -- Array type
   type Flock is array (Positive range <>) of Parrot_Acc;

   procedure Shout (F: Flock);

end Animals;
