import React, {useState} from 'react';
import { Tabs } from 'antd';
import type { TabsProps } from 'antd';
import MiddleWareViewTable from "./MiddleWareTable";
import MiddleWareTable from "./MiddleWareTable";

function MiddlerWareTabs(): JSX.Element{

  const onChange = (key: string) => {
    console.log(key);
  };



  // apiInstance
  // .services()
  // .then()
  // .catch((error: any) => {
  //   console.error(error);
  //   setServicesState('starting');
  // });




  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `HuaWei`,
      children: <MiddleWareTable />,
    },
    {
      key: '2',
      label: `Aws`,
      children: <MiddleWareTable />,
    },
    {
      key: '3',
      label: `Azure`,
      children: <MiddleWareTable />,
    },
  ];
  return(
      <Tabs defaultActiveKey="1" items={items} onChange={onChange} />
  );
}
export default MiddlerWareTabs;