
// import React, { useState } from "react";
// import { Calendar } from 'primereact/calendar';

// export default function RangeDemo() {
//     const [dates, setDates] = useState(null);

//     return (
//         <div label="Your Stay" className="card flex justify-content-center">
//             <Calendar value={dates} onChange={(e) => setDates(e.value)} selectionMode="range" readOnlyInput />
//         </div>
//     )
// }




import { DemoContainer, DemoItem } from '@mui/x-date-pickers/internals/demo';
import { LocalizationProvider } from '@mui/x-date-pickers-pro/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers-pro/AdapterDayjs';
import { DateRangePicker } from '@mui/x-date-pickers-pro/DateRangePicker';

export default function DateRangePickerCalendarProp() {
  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DemoContainer
        components={['DateRangePicker', 'DateRangePicker', 'DateRangePicker']}
      >
        <DemoItem label="Your Stay" component="DateRangePicker">
          <DateRangePicker calendars={1} />
        </DemoItem>
      </DemoContainer>
    </LocalizationProvider>
  );
}
