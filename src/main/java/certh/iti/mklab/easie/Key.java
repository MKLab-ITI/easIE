/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie;

/**
 *
 * @author vasgat
 */
public class Key<A, B> {

    private final A x;
    private final B y;

    public Key(A x, B y) {
        this.x = x;
        this.y = y;
    }
    
    public A getX(){
        return x;
    }
    
    public B getY(){
        return y;
    }
    
    @Override
    public String toString() {
        return "Key[" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key key = (Key) o;
        return x.equals(key.x) && y.equals(key.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode() + y.hashCode();
        //result = 31 * result + y.hashCode();
        return result;
    }

}