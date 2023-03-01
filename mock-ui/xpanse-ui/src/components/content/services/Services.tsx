import React from 'react';
import TabServicesChild from "./TabServicesChild";
import { HomeOutlined, UserOutlined } from '@ant-design/icons';
import { Breadcrumb } from 'antd';
import {Link} from "react-router-dom";

function Services() {
    return(
        <>
          <Breadcrumb>
            <Breadcrumb.Item><Link to={'/'}><HomeOutlined /><span>Home</span></Link></Breadcrumb.Item>
            <Breadcrumb.Item>
              <UserOutlined />
              <span>Application List</span>
            </Breadcrumb.Item>
            <Breadcrumb.Item>Application</Breadcrumb.Item>
          </Breadcrumb>
          <h1>services</h1>
          <TabServicesChild/>
        </>
    )
}
export default Services;