indirect-injector
=================
The indirect-injector simplify confusion of communication between activity and fragment, and dependencies.

[![wercker status](https://app.wercker.com/status/1af8846b7749a90ee2fabd12ebfad71e/s/ "wercker status")](https://app.wercker.com/project/bykey/1af8846b7749a90ee2fabd12ebfad71e)

## Problem

![problem](https://raw.github.com/sys1yagi/indirect-injector/master/art/problem.png)

Yes. Here's the answer.

## Setup

Gradle:

```
repositories {
    mavenCentral()
    maven { url 'https://raw.github.com/sys1yagi/indirect-injector/master/repository' }
}

dependencies {
    compile 'com.sys1yagi:indirect-injector:0.0.1'
}

```

## Usage

TODO:description

__Activity__

```
public class MainActivity extends FragmentActivity {

  ItemListFragment.Callbacks mCallback = new ItemListFragment.Callbacks() {
    @Override
    public void onItemSelected(int id) {
		//Called from fragment.
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    IndirectInjector.addDependency(this, mCallback);
    
    //initialize
    //...
  }
```

__Fragment__

TODO:description

```
public class ItemListFragment extends ListFragment {

  public interface Callbacks {
    public void onItemSelected(int id);
  }

  @Inject
  private Callbacks mCallbacks;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    IndirectInjector.inject(getActivity(), this);

    mCallbacks.onItemSelected(0);
  }
}
```

### Strong Reference

TODO:description


## License

```
Copyright 2014 Toshihiro Yagi.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
