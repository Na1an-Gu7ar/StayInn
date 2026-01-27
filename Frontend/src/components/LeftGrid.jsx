import Accordion from '@mui/material/Accordion';
import AccordionActions from '@mui/material/AccordionActions';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Button from '@mui/material/Button';
import RangeDemo from './Calender';

export default function AccordionUsage() {
    return (
        <div>
            <RangeDemo />
            {/* <Accordion>
                <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls="panel1-content"
                    id="panel1-header"
                >
                    <Typography component="span">StayIN-OUT</Typography>
                </AccordionSummary>
                <AccordionDetails>
                </AccordionDetails>
            </Accordion> */}
        </div>
    );
}
