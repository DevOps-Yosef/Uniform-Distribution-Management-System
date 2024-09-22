'use client'
import React, { Suspense, useEffect, useState } from 'react';
import { Table, TableHead, TableBody, TableCell, TableContainer, TablePagination, TableRow, LinearProgress } from '@mui/material/';
import { Button, Grid, TextField } from '@mui/material';
import Loading from './loading';
import { useFormState, useFormStatus } from 'react-dom';
import { Search } from '@mui/icons-material';
import { refreshInactiveToken, requestResourceWithCredsPost } from "../services/ServiceUtil"
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';

export default function Page() {
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<AccessLog[]>([]);
  const [name, setName] = useState("");
  const [dateFrom, setDateFrom] = useState<Dayjs | null>(null);
  const [dateTo, setDateTo] = useState<Dayjs | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi"; 
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";

  let apiAccessLogIndexUrl = redirectURIApi + "/admin/index";
  let apiAccessLogCountUrl = redirectURIApi + "/admin/count";

  const initialState = {
  }

  useEffect(() => {
    try {
      refreshInactiveToken().then(data => {
    });
    } catch (error) {
    }
  }, []);


  useEffect(() => {
    const fetchData = async () => {
      const queryParams = {
        dateFrom: dateFrom,
        dateTo: dateTo,
        name: name,
        page: page,
        rowsPerPage: rowsPerPage
      };
      await refreshInactiveToken();
      const dataIndex = await requestResourceWithCredsPost(apiAccessLogIndexUrl, queryParams);
      setRows(dataIndex);
      const dataCount = await requestResourceWithCredsPost(apiAccessLogCountUrl, queryParams);
      setTotalCount(dataCount);
      setIsLoading(false);
    };
    fetchData();
  }, [page, rowsPerPage]);


  const [state, formAction] = useFormState(searchAccessLog, initialState);

  function SearchPositionButton() {
    const { pending } = useFormStatus();

    return (
      <>
        <Button variant="contained" endIcon={<Search />} type='submit' aria-disabled={pending} size='large'>
          Search
        </Button>
      </>
    )
  }

  async function searchAccessLog(prevState: any, formData: FormData) {
    setPage(0);
    // let name = formData.get('name') === null ? "" : formData.get('name').toString();
    let dateFrom = dayjs(formData.get('dateFrom') === null ? "" : formData.get('dateFrom').toString());
    let dateTo = dayjs(formData.get('dateTo') === null ? "" : formData.get('dateTo').toString());
    const queryParams = {
      dateFrom: dateFrom,
      dateTo: dateTo,
      page: page,
      rowsPerPage: rowsPerPage
    };
    const dataIndex = await requestResourceWithCredsPost(apiAccessLogIndexUrl, queryParams);
    setRows(dataIndex);
    const dataCount = await requestResourceWithCredsPost(apiAccessLogCountUrl, queryParams);
    setTotalCount(dataCount);
    setName(name);
    setDateFrom(dateFrom);
    setDateTo(dateTo);
  }


  const handleChangePage = async (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };
  const [value, setValue] = React.useState<Dayjs | null>(dayjs('2022-04-17T15:30'));

  if (isLoading) {
    return <div><LinearProgress /></div>;
  }

  return (
    <>
      <Suspense fallback={<Loading />} >
        <Grid>
          <form action={formAction}>
            <Grid container spacing={2}>
              <Grid item xs={3}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DatePicker name="dateFrom"
                    label="Date From" value={dateFrom}
                    onChange={(newValue) => setDateFrom(newValue)}
                  />
                </LocalizationProvider>
              </Grid>
              <Grid item xs={3}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <DatePicker name="dateTo"
                    label="Date To" value={dateTo}
                    onChange={(newValue) => setDateTo(newValue)}
                  />
                </LocalizationProvider>
              </Grid>
              <Grid item xs={2}>
                <SearchPositionButton />
              </Grid>
            </Grid>

          </form>
          <TableContainer>
            <Table>
              <TableHead>
                {(
                  <TableRow>
                    <TableCell>Date</TableCell>
                    <TableCell>Name</TableCell>
                    <TableCell>Operation</TableCell>
                  </TableRow>
                )}
              </TableHead>
              <TableBody>
                {rows && (rows.map(row => (
                  <TableRow key={row.id}>
                    <TableCell>{dayjs(row.date).format("MMM/DD/YYYY hh:mm:ss A")}</TableCell>
                    <TableCell>{row.username}</TableCell>
                    <TableCell>{row.operation}</TableCell>
                  </TableRow>
                )))}
              </TableBody>
            </Table>
            <TablePagination
              rowsPerPageOptions={[4, 10, 25]}
              component="div"
              count={totalCount}
              rowsPerPage={rowsPerPage}
              page={page}
              onPageChange={handleChangePage}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          </TableContainer>
        </Grid>
      </Suspense>
    </>
  );
}
