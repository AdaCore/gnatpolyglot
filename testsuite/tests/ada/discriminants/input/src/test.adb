with Ada.Text_IO; use Ada.Text_IO;

package body Test is

    function Make_R (I : Integer) return R is
    begin
        return (A => I, others => <>);
    end Make_R;

    procedure Print (V : Root) is null;

end Test;
