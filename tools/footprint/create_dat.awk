BEGIN     { COUNT = 1
            if (TYPE == "") TYPE = "vms";
          }

/^[0-9]/  { if      (TYPE == "vms") print COUNT, $1;
            else if (TYPE == "vmd") print COUNT, $2;
            else if (TYPE == "vml") print COUNT, $3;
            else if (TYPE == "rss") print COUNT, $4;

            COUNT = COUNT + 1;
          }

/^ /      { print COUNT, "0";
            COUNT = COUNT + 1; \
          }


