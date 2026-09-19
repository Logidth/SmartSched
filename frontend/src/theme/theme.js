import { createTheme } from "@mui/material/styles";

const theme = createTheme({

    palette:{

        primary:{
            main:"#0D47A1"
        },

        secondary:{
            main:"#1565C0"
        },

        background:{
            default:"#F4F7FC"
        }

    },

    typography:{

        fontFamily:"Poppins, Roboto, Arial"

    },

    shape:{
        borderRadius:12
    }

});

export default theme;