import { Card, CardContent, Typography } from "@mui/material";

export default function StatCard({

    title,

    value,

    color

}){

    return(

        <Card
            sx={{
                borderLeft:`6px solid ${color}`,
                borderRadius:3
            }}
        >

            <CardContent>

                <Typography
                    color="gray"
                >
                    {title}
                </Typography>

                <Typography
                    variant="h4"
                    fontWeight="bold"
                >
                    {value}
                </Typography>

            </CardContent>

        </Card>

    );

}