package Expect is

   type Process_Descriptor is tagged private;

private

   type Pipe_Type is null record;

   procedure Proc
     (Pid   : Process_Descriptor;
      Pipe  : Pipe_Type);

   type Process_Descriptor is tagged null record;

end Expect;
