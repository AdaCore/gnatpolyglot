package body P is

   function Create return Inst_2.R is
   begin
      pragma Warnings (Off, "aggregate not fully initialized");
      return (others => <>);
      pragma Warnings (On, "aggregate not fully initialized");
   end Create;

end P;