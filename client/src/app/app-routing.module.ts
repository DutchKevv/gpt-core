import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AskGenericComponent } from './components/ask-generic/ask-generic.component';

const routes: Routes = [
  {
    path: '',
    component: AskGenericComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
