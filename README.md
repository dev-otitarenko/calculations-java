# Running simple jscript-constructions on the server side. Playing with Java features

Input in JSON format, The set of data with unique fields names:
```
{
"Field1": 100,
"Field2": 20,
"Field3": 300
}
```
Example calculation formulas:
```sh
Field100 = Field1 + Field2
Field101 = Field1 - Field2
Field102 = Field1 * Field3 + Field2
Field103 = (Field3 - Field1) / Field2
Field200 = Min.MAX(Field1, Field2, Field3)
Field201 = Min.MIN(Field1, Field2, Field3)
Field202 = Min.SUM(Field1, Field2, Field3)
Field203 = Min.AVG(Field1, Field2, Field3)
....
```

After calculations, output:
```
{
"Field1": 100,
"Field2": 20,
"Field3": 300,

"Field100": 120,
"Field101": 80,
"Field102": 2300,
"Field103": 10,

"Field200": 300,
"Field201": 20,
"Field202": 420,
}
```

# 