import './App.css';
import React from 'react';
import {Route, Routes} from 'react-router-dom';
import './styles/app.css';
import Home from './components/content/Home';
import LoginScreen from './components/content/LoginScreen';
import Protected from './components/content/ProtectedRoute';
import {homePageRoute} from './components/utils/constants';
import Services from "./components/content/services/Services";
import Billing from "./components/content/billing/Billing";
import IntegrationEndpoints from "./components/content/integration/IntegrationEndpoints";
function App() {
  return (
      <Routes>
        <Route
            path={homePageRoute}
            element={
              <Protected>
                <Home/>
              </Protected>
            }
        />
        <Route path={'/services'}
               element={
                 <Protected>
                   <Services/>
                 </Protected>
               }
        />
        <Route path={'/billing'}
               element={
                 <Protected>
                   <Billing/>
                 </Protected>
               }
        />
        <Route path={'/billing'}
               element={
                 <Protected>
                   <Billing/>
                 </Protected>
               }
        />
        <Route path={'/integration-endpoints'}
               element={
                 <Protected>
                   <IntegrationEndpoints/>
                 </Protected>
               }
        />
        <Route path="*" element={<LoginScreen/>}/>
      </Routes>
  );
}

export default App;
