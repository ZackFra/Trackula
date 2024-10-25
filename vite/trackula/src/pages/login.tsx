import { Form, FormItem, FormControl, FormField, FormLabel } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { ControllerRenderProps, useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from "@hookform/resolvers/zod"

import { login } from '@/api'

const formSchema = z.object({
    username: z.string().min(5, { message: 'Username must be at least 5 characters long' }),
    password: z.string().min(8, { message: 'Password must be at least 8 characters long' })
})

type ItemProps = {
    label: string
    placeholder: string
    field: ControllerRenderProps<Zod.infer<typeof formSchema>>
    description: string,
    type?: string
}

const Item = ({ label, placeholder, field, type="text" }: ItemProps) => {
    return (
        <FormItem>
            <FormLabel>{label}</FormLabel>
            <FormControl>
                <Input type={type} placeholder={placeholder} {...field} />
            </FormControl>
        </FormItem>
    )
}

const LoginForm = () => {
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            username: "",
            password: ""
        }
    })
    const onSubmit = async (data : z.infer<typeof formSchema>) => {
        const response = await login(data)
        console.log(response)
    }
    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className='space-y-8'>
            <FormField 
                control={form.control} 
                name="username"
                render={({ field }) => (
                    <Item 
                        label="Username" 
                        placeholder="Enter your username" 
                        field={field} 
                        description="Your username must be at least 8 characters long" 
                    />
                )} />
            <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                    <Item
                        label="Password"
                        placeholder="Enter your password"
                        field={field}
                        description="Your password must be at least 8 characters long"
                        type="password"
                    />
                )} />
                <Button type="submit">Submit</Button>
            </form>
        </Form>
    )
}

export default LoginForm