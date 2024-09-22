'use client'
import { Add, Search } from '@mui/icons-material';
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, FormControl, Grid, IconButton, InputLabel, LinearProgress, MenuItem, OutlinedInput, Paper, Select, SelectChangeEvent, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { requestResourceWithCredsPost } from "../services/ServiceUtil";
import FeedbackIcon from '@mui/icons-material/Feedback';
import { refreshInactiveToken } from "../services/ServiceUtil";


export default function Page() {
  const [rows, setRows] = useState<Entitlement[]>([]);
  const [imsItems, setImsItems] = useState<ImsItem[]>([]);
  const [positionCategories, setPositionCategories] = useState<PositionCategory[]>([]);
  const [selectedPositionCategory, setSelectedPositionCategory] = useState('');
  const [selectedEntitlement, setSelectedEntitlement] = useState<Entitlement>();
  const [selectedEntitlementUniformItems, setSelectedEntitlementUniformItems] = useState<EntitlementUniformItems>();
  const [isLoading, setIsLoading] = useState(true);
  const [isForbidden, setIsForbidden] = useState(false);
  const [openEdit, setOpenEdit] = useState(false);
  const [openRemark, setOpenRemark] = useState(false);
  const [openAddPosition, setOpenAddPosition] = useState(false);
  const [openAddEntitlement, setOpenAddEntitlement] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState('');

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";

  let apiPositionsEditUrl = redirectURIApi + "/positions/officer/edit";
  let apiPositionsCreatePositionUrl = redirectURIApi + "/positions/officer/createposition";
  let apiPositionsCreateEntitlementUrl = redirectURIApi + "/positions/officer/createentitlement";
  let apiPositionsIndexUrl = redirectURIApi + "/positions/common/index";
  let apiPositionsItemsUrl = redirectURIApi + "/positions/officer/items";
  let apiPositionsCommonPositionCategories = redirectURIApi + "/positions/common/positionCategories";

  useEffect(() => {
    try {
      refreshInactiveToken().then(data => {
    });
    } catch (error) {
    }
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      await refreshInactiveToken();
      if (selectedPositionCategory) {
        const queryParams = {
          positionCategoryName: selectedPositionCategory,
        };
        const rowData = await requestResourceWithCredsPost(apiPositionsIndexUrl, queryParams);
        if (rowData.status && rowData.status === 403) {
          setIsForbidden(true);
        }
        else {
          setRows(rowData);
          setIsLoading(false);
        }
      }
      else {
        const data = await requestResourceWithCredsPost(apiPositionsCommonPositionCategories, []);
        const queryParams = {
          positionCategoryName: data.length > 0 ? data[0].name : '',
        };
        setPositionCategories(data);
        setSelectedPositionCategory(data.length > 0 ? data[0].name : '');
        const rowData = await requestResourceWithCredsPost(apiPositionsIndexUrl, queryParams);
        if (rowData.status && rowData.status === 403) {
          setIsForbidden(true);
        }
        else {
          setRows(rowData);
          setIsLoading(false);
        }
      }
    };
    fetchData();
  }, [selectedPositionCategory]);


  const handlePositionCategoryChange = async (event: SelectChangeEvent) => {
    setSelectedPositionCategory(event.target.value);
  };

  function PositionCategorySelectInput() {

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
      PaperProps: {
        style: {
          maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
          width: 250,
        },
      },
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="category-label">Category</InputLabel>
          <Select
            labelId="category-label"
            value={selectedPositionCategory}
            onChange={handlePositionCategoryChange}
            input={<OutlinedInput label="Category" />}
            MenuProps={MenuProps}
          >
            {positionCategories && positionCategories.map((positionCategory) => (
              <MenuItem
                key={positionCategory.id}
                value={positionCategory.name}
              >
                {positionCategory.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </div>
    );
  }

  const handleEditClose = () => {
    setOpenEdit(false);
  };

  const handleAddEntitlementClose = () => {
    setOpenAddEntitlement(false);
  };

  const handleAddPositionClose = () => {
    setOpenAddPosition(false);
  };

  const handleRemarkClose = () => {
    setOpenRemark(false);
  };

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };



  const handleAddPositionButtonClick = () => {
    setOpenAddPosition(true);
  };


  const handleAddEntitlementButtonClick = async () => {
    if (!selectedPositionCategory) {
      setActionStatus('add-button-fail');
      setOpenActionStatus(true);
    }
    else {
      const queryParams = {
        positionCategoryName: selectedPositionCategory,
      };
      const data = await requestResourceWithCredsPost(apiPositionsItemsUrl, queryParams);
      data.map(item => {
        if (item.name === "Fabric for suit") {
          return { ...item, name: 'Suit' };
        }
        return item;
      });
      setImsItems(data);
      setOpenAddEntitlement(true);
    }
  }

  const handleEditButtonClick = (row, rowX) => {
    setSelectedEntitlement(row);
    setSelectedEntitlementUniformItems(rowX);
    setOpenEdit(true);
  };


  const handleRemarkButtonClick = (row) => {
    setSelectedEntitlement(row);
    setOpenRemark(true);
  };

  function EntitlementToEditDialog() {
    const [quantity, setQuantity] = useState("");
    const [period, setPeriod] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    useEffect(() => {
      if (openEdit) {
        setQuantity(selectedEntitlementUniformItems.quantity);
        setPeriod(selectedEntitlementUniformItems.period);
      }
    }, [openEdit]);

    const handleEditEmployeeSubmitForApproval = async (event) => {
      if (quantity && period) {
        let entitlementUniformItems: EntitlementUniformItems = {
          id: selectedEntitlementUniformItems.id,
          quantity: quantity,
          period: period,
          imsItemId: '',
          itemName: '',
          entitlement: undefined
        }
        const entitlementUniformItemsList: EntitlementUniformItems[] = [
          entitlementUniformItems
        ];

        const entitlementToEdit: Entitlement = {
          id: selectedEntitlement.id,
          createdBy: '',
          createdDate: undefined,
          approvedBy: '',
          approvedDate: undefined,
          email: false,
          remark: '',
          positionCategory: undefined,
          entitlementUniformItemsCollection: entitlementUniformItemsList,
          positionCategoryName: '',
          luStatusDTO: undefined,
          page: '',
          rowsPerPage: '',
          imsItemId: 0,
          itemName: '',
          quantity: 0,
          period: 0
        };
        setErrorMessageHidden(false);
        try {
          const response = await requestResourceWithCredsPost(apiPositionsEditUrl, entitlementToEdit);
          //SUCCESS
          if (response) {
            setOpenEdit(false);
            setActionStatus('form-success');
            setOpenActionStatus(true);
            setIsLoading(true);
            const queryParams = {
              positionCategoryName: selectedPositionCategory,
            };
            const data = await requestResourceWithCredsPost(apiPositionsIndexUrl, queryParams);
            if (data) {
              setRows(data);
              setIsLoading(false);
            }
          }
          else {
            setOpenEdit(false);
            setActionStatus('form-fail');
            setOpenActionStatus(true);
          }
        } catch (error) {
          //ERROR
          setOpenEdit(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleQuanityChange = (event) => {
      setQuantity(event.target.value);
    };

    const handlePeriodChange = (event) => {
      setPeriod(event.target.value);
    };

    return (
      <Dialog
        open={openEdit}
        onClose={handleEditClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Edit Entitlement</span></Grid>
          <Grid item xs={12}>
            {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
              <AlertTitle>Error</AlertTitle>
              <Typography>Please fill the required fields marked(*)</Typography>
            </Alert>
            }
          </Grid>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedEntitlement?.positionCategory?.name}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedEntitlementUniformItems?.itemName}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="quantity"
                  label="Quantity"
                  value={quantity}
                  onChange={handleQuanityChange}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="period"
                  label="Period"
                  value={period}
                  onChange={handlePeriodChange}
                />
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleEditEmployeeSubmitForApproval}>Submit For Approval</Button>
        </DialogActions>
      </Dialog>
    );
  }

  function EntitlementToAddDialog() {
    const [selectedItem, setSelectedItem] = useState<ImsItem>(null);
    const [entitlementUniformItemsRows, setEntitlementUniformItemsRows] = useState<EntitlementUniformItems[]>([]);
    const [quantity, setQuantity] = useState("");
    const [period, setPeriod] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);


    function ImsItemSelectInputAdd() {

      const ITEM_HEIGHT = 48;
      const ITEM_PADDING_TOP = 8;
      const MenuProps = {
        PaperProps: {
          style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
          },
        },
      };

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="item-label">Item *</InputLabel>
            <Select
              labelId="item-label"
              value={selectedItem ? selectedItem.id : ''}
              onChange={handleChange}
              label="Item *"
              MenuProps={MenuProps}
            >
              {imsItems && imsItems.map((imsItem) => (
                <MenuItem
                  key={imsItem.id}
                  value={imsItem.id}
                >
                  {imsItem.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }


    const handleAddToList = (event) => {
      if (selectedItem && quantity && period) {
        setErrorMessageHidden(false);
        let entitlementUniformItems: EntitlementUniformItems = {
          id: "",
          imsItemId: selectedItem.id,
          itemName: selectedItem.name,
          quantity: quantity,
          period: period,
          entitlement: undefined
        }
        entitlementUniformItemsRows.push(entitlementUniformItems);
        setSelectedItem(null);
        setQuantity("");
        setPeriod("");
        const tempSelectedItem = imsItems.find(item => item.id === selectedItem.id);
        let indexToRemove = imsItems.indexOf(tempSelectedItem);
        if (indexToRemove !== -1) {
          imsItems.splice(indexToRemove, 1);
        }
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleChange = (event: SelectChangeEvent) => {
      const selectedMaterial = imsItems.find(item => item.id === event.target.value);
      setSelectedItem(selectedMaterial);
    };

    const handleAddEntitlementSubmitForApproval = async (event) => {
      let positionCategory: PositionCategory = {
        id: '',
        name: selectedPositionCategory,
        description: ''
      }

      let entitlement: Entitlement = {
        id: '',
        createdBy: '',
        createdDate: undefined,
        approvedBy: '',
        approvedDate: undefined,
        email: false,
        remark: '',
        positionCategory: positionCategory,
        entitlementUniformItemsCollection: entitlementUniformItemsRows,
        positionCategoryName: '',
        luStatusDTO: undefined,
        page: '',
        rowsPerPage: '',
        imsItemId: 0,
        itemName: '',
        quantity: 0,
        period: 0
      }

      if (entitlement.entitlementUniformItemsCollection && entitlement.entitlementUniformItemsCollection?.length > 0) {
        const response = await requestResourceWithCredsPost(apiPositionsCreateEntitlementUrl, entitlement);
        //SUCCESS
        setOpenAddEntitlement(false);
        setActionStatus('form-success');
        setOpenActionStatus(true);
        setIsLoading(true);
        try {
          if (response) {
            const data = await requestResourceWithCredsPost(apiPositionsCommonPositionCategories, []);
            const queryParams = {
              positionCategoryName: selectedPositionCategory,
            };
            setPositionCategories(data);
            setSelectedPositionCategory(data.length > 0 ? data[0].name : '');
            const dataIndex = await requestResourceWithCredsPost(apiPositionsIndexUrl, queryParams);
            if (dataIndex) {
              setRows(dataIndex);
              setIsLoading(false);
            }
          }
          else {
            setOpenAddEntitlement(false);
            setActionStatus('form-fail');
            setOpenActionStatus(true);
          }
        }
        catch (error) {
          //ERROR
          setOpenAddEntitlement(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
    };

    const handleQuanityChange = (event) => {
      setQuantity(event.target.value);
    };

    const handlePeriodChange = (event) => {
      setPeriod(event.target.value);
    };

    return (
      <Dialog open={openAddEntitlement}
        onClose={handleAddEntitlementClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Add Entitlement</span></Grid>
          <Grid item xs={12}>
            {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
              <AlertTitle>Error</AlertTitle>
              <Typography>Please fill the required fields marked(*)</Typography>
            </Alert>
            }
          </Grid>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedPositionCategory}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <ImsItemSelectInputAdd />
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="quantity"
                  label="Quantity"
                  value={quantity}
                  onChange={handleQuanityChange}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="period"
                  label="Period"
                  value={period}
                  onChange={handlePeriodChange}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }} size="small">
                <Button variant="contained" type='submit' onClick={handleAddToList}>
                  Add To List
                </Button>
              </FormControl>
            </Grid>
            {/* ################################################################################################### */}
            <Grid item xs={12}><Divider>Item List</Divider></Grid>
            <TableContainer>
              <Table>
                <TableHead>
                  {(
                    <TableRow>
                      <TableCell>Item</TableCell>
                      <TableCell>Quantity</TableCell>
                      <TableCell>Period</TableCell>
                    </TableRow>
                  )}
                </TableHead>
                <TableBody>
                  {entitlementUniformItemsRows && entitlementUniformItemsRows.map(row => (

                    <TableRow key={row.imsItemId}>
                      <TableCell>{row.itemName}</TableCell>
                      <TableCell>{row.quantity}</TableCell>
                      <TableCell>{row.period}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            {/* #################################################################################################### */}
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleAddEntitlementClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleAddEntitlementSubmitForApproval}>Submit For Approval</Button>
        </DialogActions>
      </Dialog>
    );
  }

  function PositionToAddDialog() {
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);


    const handleAddPosition = async (event) => {
      if (name && description) {
        const positionToAdd: PositionCategory = {
          id: '',
          name: name,
          description: description
        };
        setErrorMessageHidden(false);
        try {
          const response = await requestResourceWithCredsPost(apiPositionsCreatePositionUrl, positionToAdd);
          //SUCCESS
          if (response) {
            setOpenAddPosition(false);
            setActionStatus('form-success');
            setOpenActionStatus(true);
            setIsLoading(true);
            const data = await requestResourceWithCredsPost(apiPositionsCommonPositionCategories, []);
            setPositionCategories(data);
            const queryParams = {
              positionCategoryName: selectedPositionCategory,
            };
            const dataIndex = await requestResourceWithCredsPost(apiPositionsIndexUrl, queryParams);
            if (dataIndex) {
              setRows(dataIndex);
              setIsLoading(false);
            }
          }
          else {
            setOpenAddPosition(false);
            setActionStatus('form-fail');
            setOpenActionStatus(true);
          }
        } catch (error) {
          //ERROR
          setOpenAddPosition(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleNameChange = (event) => {
      setName(event.target.value);
    };

    const handleDescriptionChange = (event) => {
      setDescription(event.target.value);
    };

    return (
      <Dialog open={openAddPosition}
        onClose={handleAddPositionClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Add Position</span></Grid>
          <Grid item xs={12}>
            {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
              <AlertTitle>Error</AlertTitle>
              <Typography>Please fill the required fields marked(*)</Typography>
            </Alert>
            }
          </Grid>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="name"
                  label="Name"
                  value={name}
                  onChange={handleNameChange}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="description"
                  label="Description"
                  value={description}
                  onChange={handleDescriptionChange}
                />
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleAddPositionClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleAddPosition}>Save</Button>
        </DialogActions>
      </Dialog>
    );
  }


  function ActionStatusDialog() {
    return (
      <Dialog open={openActionStatus} onClose={handleActionStatusClose}>
        <DialogTitle align='center'>Action Status</DialogTitle>
        <DialogContent>
          {actionStatus && actionStatus === 'form-success' && <Alert severity="success">Action Success.</Alert>}
          {actionStatus && actionStatus === 'form-fail' && <Alert severity="error">Action Failure.</Alert>}
          {actionStatus && actionStatus === 'add-button-fail' && <Alert severity="error">Select Category.</Alert>}
        </DialogContent>
      </Dialog>
    );
  }

  function RemarkDialog() {
    return (
      <Dialog open={openRemark} onClose={handleRemarkClose}>
        <DialogTitle align='center'>Remark</DialogTitle>
        <DialogContent>
          <Typography>{selectedEntitlement?.remark}</Typography>
        </DialogContent>
      </Dialog>
    );
  }

  const NoWrapPadTblCell = styled(TableCell)(({ theme }) => ({
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  }));

  if (isForbidden) {
    return (
      <div>
        <Alert variant="outlined" severity="error">
          Access is forbidden.
        </Alert>
      </div>
    )
  }

  if (isLoading) {
    return <div><LinearProgress /></div>;
  }



  return (
    <>
      <div>
        <EntitlementToAddDialog />
        <EntitlementToEditDialog />
        <ActionStatusDialog />
        <PositionToAddDialog />
        <RemarkDialog />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={4}>
          <PositionCategorySelectInput />
        </Grid>
        <Grid item xs={false} sm={2} />
        <Grid item xs={3} >
          <Button variant="contained" type='button' endIcon={<Add />} onClick={() => handleAddPositionButtonClick()} >Add Position</Button>
        </Grid>
        <Grid item xs={3}>
          <Button variant="contained" type='button' endIcon={<Add />} onClick={() => handleAddEntitlementButtonClick()} >Add Entitlement</Button>
        </Grid>
      </Grid>

      <Grid>


        <>

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <NoWrapPadTblCell>Name</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Item</NoWrapPadTblCell>
                  <TableCell>Quantity</TableCell>
                  <TableCell>Period</TableCell>
                  <TableCell>Approved</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  row.entitlementUniformItemsCollection.map((rowX) => (
                    <TableRow>
                      <NoWrapPadTblCell>{row.positionCategory?.name}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{rowX.itemName}</NoWrapPadTblCell>
                      <TableCell>{rowX.quantity}</TableCell>
                      <TableCell>{rowX.period + " Years"}</TableCell>
                      <TableCell>{row.approvedBy}</TableCell>
                      <TableCell>{dayjs(row.approvedDate).format("MMM/DD/YYYY")}</TableCell>
                      <TableCell>
                        <Grid container spacing={1}>
                          <Grid item xs={6}>
                            <Button variant="contained" type='button' size='small' onClick={() => handleEditButtonClick(row, rowX)}>Edit</Button>
                            {row.luStatusDTO.name === "Rejected" && <IconButton color="error" size='large' onClick={() => handleRemarkButtonClick(row)}><FeedbackIcon /></IconButton>}
                          </Grid>
                        </Grid>
                      </TableCell>
                    </TableRow>
                  ))
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      </Grid>
    </>
  );
}
