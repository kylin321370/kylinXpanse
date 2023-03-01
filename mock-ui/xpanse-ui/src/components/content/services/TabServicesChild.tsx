import React from 'react';
import { Tabs } from 'antd';

function TabServicesChild(props: any) {
  return(
      <>
        <Tabs defaultActiveKey="1" items={props.items} />;
        <Tabs defaultActiveKey="1" items={props.term} />;
      </>
  )
}
export default TabServicesChild;